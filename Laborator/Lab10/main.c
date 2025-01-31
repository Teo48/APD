#include<mpi.h>
#include<stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>

#define CONVERGENCE_COEF 100
#define MAX(x, y) (((x) > (y)) ? (x) : (y))
/**
 * Run: mpirun -np 12 ./a.out
 */

static int num_neigh;
static int *neigh;

void read_neighbours(int rank) {
    FILE *fp;
    char file_name[15];
    sprintf(file_name, "./files/%d.in", rank);

    fp = fopen(file_name, "r");
	fscanf(fp, "%d", &num_neigh);

	neigh = malloc(sizeof(int) * num_neigh);

	for (size_t i = 0; i < num_neigh; i++)
		fscanf(fp, "%d", &neigh[i]);
}

int* get_dst(int rank, int numProcs, int leader) {
	MPI_Status status;
	MPI_Request request;

	/* Vectori de parinti */
	int *v = malloc(sizeof(int) * numProcs);
	int *vRecv = malloc(sizeof(int) * numProcs);

	memset(v, -1, sizeof(int) * numProcs);
	memset(vRecv, -1, sizeof(int) * numProcs);
	
	if (rank == leader)
		v[rank] = -1;
	else {
		/* Daca procesul curent nu este liderul, inseamna ca va astepta un mesaj de la un parinte */
		MPI_Recv(vRecv, numProcs, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &status);
		v[rank] = status.MPI_SOURCE;
	}


	/*
	*  TODO2: Pentru fiecare proces vecin care nu este parintele procesului curent,
	*		  voi trimite vectorul de parinti propriu. 
	*/
	for (int i = 0; i < num_neigh; ++i) {
		if (neigh[i] != v[rank]) {
			MPI_Send(v, numProcs, MPI_INT, neigh[i], 0, MPI_COMM_WORLD);
		}
	}

	/*
	*  TODO2: Vom astepta de la fiecare proces vecin care nu este parintele procesului curent 
	*		  vectorul de parinti si actualizam vectorul propriu de parinti daca exista informatii aditionale
	*/
	for (int i = 0; i < num_neigh; ++i) {
		if (neigh[i] != v[rank]) {
			MPI_Recv(vRecv, numProcs, MPI_INT, neigh[i], 0, MPI_COMM_WORLD, &status);
			for (int j = 0; j < numProcs; ++j) {
				if (v[j] == -1 && vRecv[j] != -1) {
					v[j] = vRecv[j];
				}
			}
		}
	}

	/*
	*  TODO2: Topologia fiind deja stabilita, orice proces ce nu este lider va propaga
	* 		  vectorul de vecini parintelui lui si va astepta topologia completa de la acesta
	*/
	if (rank != leader) {
		MPI_Send(v, numProcs, MPI_INT, v[rank], 0, MPI_COMM_WORLD);
		MPI_Recv(v, numProcs, MPI_INT, v[rank], 0, MPI_COMM_WORLD, &status);
	}


	/*
	*  TODO2: Procesul curent va trimite doar copiilor lui topologia completa
	*/
	for (int i = 0; i < numProcs; ++i) {
		if (v[i] == rank) {
			MPI_Send(v, numProcs, MPI_INT, i, 0, MPI_COMM_WORLD);
		}
	}


	for (int i = 0; i < numProcs && rank == leader; i++) {
		printf("The node %d has the parent %d\n", i, v[i]);
	}

	return v;
}

int leader_chosing(int rank, int nProcesses) {
	int leader = -1;
	int q;
	leader = rank;
	
	/* Executam acest pas pana ajungem la convergenta */
	for (int k = 0; k < CONVERGENCE_COEF; k++) {
		/* TODO1: Pentru fiecare vecin, vom trimite liderul pe care il cunosc 
		* 		 si voi astepta un mesaj de la orice vecin
		* 		 Daca liderul e mai mare decat al meu, il actualizez pe al meu
		*/
		for (int i = 0; i < num_neigh; ++i) {
			MPI_Send(&leader, 1, MPI_INT, neigh[i], MPI_TAG_UB, MPI_COMM_WORLD);
			int neighbour_leader;
			MPI_Recv(&neighbour_leader, 1, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			leader = MAX(neighbour_leader, leader);
		}
	}

	MPI_Barrier(MPI_COMM_WORLD);
	printf("%i/%i: leader is %i\n", rank, nProcesses, leader);

	return leader;
}

int get_number_of_nodes(int rank, int leader) {
	
	double val;
	if (leader == rank) {
		val = 1.0;
	} else {
		val = 0.0;
	}

	double recvd = 0;
	/* Executam acest pas pana ajungem la convergenta */
	for (int k = 0; k < CONVERGENCE_COEF; k++) {
		/* TODO3: Pentru fiecare vecin, vom trimite valoarea pe care o cunosc
		* 		 si voi astepta un mesaj de la el
		* 		 Cu valoarea primita, actualizam valoarea cunoscuta ca fiind
		* 		 media dintre cele 2
		*/
		for (int i = 0; i < num_neigh; ++i) {
			MPI_Send(&val, 1, MPI_DOUBLE, neigh[i], MPI_TAG_UB, MPI_COMM_WORLD);
			MPI_Recv(&recvd, 1, MPI_DOUBLE, neigh[i], 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			val = (recvd + val) / 2;
		}
	}
	
	MPI_Barrier(MPI_COMM_WORLD);
	
	return (float)(1 / val);
}

// Screw alocare dinamica in MPI. Crapa cu matricile alocate dinamic
// asa ca le-am alocat static si returnez adresa uneia noua dupa ce calculez
// cele necesare

int ** get_topology(int rank, int nProcesses, int * parents, int leader) {
	// Later edit: Dupa lupte seculare, orgoliu si stackoverflow,
	// trebuie sa fie alocate intr-o zona continua ca sa poti trimite
	// felul in care erau alocate in schelet nu garanta asta.
	int *data1 = (int *)calloc(nProcesses * nProcesses, sizeof(*data1));
	int *data2 = (int *)calloc(nProcesses * nProcesses, sizeof(*data2));
	int **topology = (int **)calloc(nProcesses, sizeof(*topology));
	int **vTopology = (int **)calloc(nProcesses, sizeof(*vTopology));
	for (int i = 0; i < nProcesses; ++i) {
		topology[i] = &(data1[nProcesses * i]);
		vTopology[i] = &(data2[nProcesses * i]);
	}
	// int topology[nProcesses][nProcesses];
    // int vTopology[nProcesses][nProcesses];
	// for (int i = 0; i < nProcesses; ++i) {
	// 	for (int j = 0; j < nProcesses; ++j) {
	// 		topology[i][j] = 0;
	// 		vTopology[i][j] = 0;
	// 	}
	// }
	for (size_t i = 0; i < num_neigh; i++) {
		topology[rank][neigh[i]] = 1;
	}

	/* TODO4: Primim informatii de la toti copii si actualizam matricea de topologie */

	for (int i = 0; i < nProcesses; ++i) {
		if (parents[i] == rank) {
			MPI_Recv(&(vTopology[0][0]), nProcesses * nProcesses, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			for (int j = 0; j < nProcesses; ++j) {
				for (int k = 0; k < nProcesses; ++k) {
					if (vTopology[j][k] != 0 && topology[j][k] == 0) {
						topology[j][k] = vTopology[j][k];
					}
				}
			}
		}
	}

	/* TODO4: Propagam matricea proprie catre parinte */
	
	if (rank != leader) {
		MPI_Send(&(topology[0][0]), nProcesses * nProcesses, MPI_INT, parents[rank], 0, MPI_COMM_WORLD);
		MPI_Recv(&(topology[0][0]), nProcesses * nProcesses, MPI_INT, parents[rank], 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	}
	/* TODO4: Daca nu suntem liderul, asteptam topologia completa de la parinte  */
	
	
	/* TODO4: Trimitem topologia completa copiilor */
	for (int i = 0; i < nProcesses; ++i) {
		if (parents[i] == rank) {
			MPI_Send(&(topology[0][0]), nProcesses * nProcesses, MPI_INT, i, 0, MPI_COMM_WORLD);
		}
	}
	
	// int ** ntopology = malloc(sizeof(int*) * nProcesses);
	// for (size_t i = 0; i < nProcesses; i++) {
	// 	ntopology[i] = calloc(sizeof(int), nProcesses);
	// }

	// for (int j = 0; j < nProcesses; ++j) {
	// 	for (int k = 0; k < nProcesses; ++k) {
	// 		ntopology[j][k] = topology[j][k];
	// 	}
	// }
	
	// return ntopology

	return topology;
}

int main(int argc, char * argv[]) {
	int rank, nProcesses, num_procs, leader;
	int *parents, **topology;

	MPI_Init(&argc, &argv);
	MPI_Status status;
	MPI_Request request;

	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &nProcesses);

	if (nProcesses != 12) {
		printf("please run with: mpirun --oversubscribe -np 12 %s\n", argv[0]);
		MPI_Finalize();	
		exit(0);
	}
	 
	read_neighbours(rank);
	leader = leader_chosing(rank, nProcesses);
	
	MPI_Barrier(MPI_COMM_WORLD);

	parents = get_dst(rank, nProcesses, leader);

	MPI_Barrier(MPI_COMM_WORLD);

	num_procs = get_number_of_nodes(rank, leader);
	
	printf("%d/%d There are %d processes\n", rank, nProcesses,num_procs);

	topology = get_topology(rank, nProcesses, parents, leader);

	for (size_t i = 0; i < nProcesses && rank == 0; i++)
	{
		for (size_t j = 0; j < nProcesses; j++)
		{
			printf("%2d ", topology[i][j]);	
		}
		printf("\n");
	}
	
	MPI_Finalize();
	return 0;
}