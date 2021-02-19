#include<mpi.h>
#include<stdio.h>
#include<stdlib.h>

int N;

void compareVectors(int * a, int * b) {
	// DO NOT MODIFY
	int i;
	for(i = 0; i < N; i++) {
		if(a[i]!=b[i]) {
			printf("Sorted incorrectly\n");
			return;
		}
	}
	printf("Sorted correctly\n");
}

void displayVector(int * v) {
	// DO NOT MODIFY
	int i;
	for(i = 0; i < N; i++) {
		printf("%d ", v[i]);
	}
	printf("\n");
}

int cmp(const void *a, const void *b) {
	// DO NOT MODIFY
	int A = *(int*)a;
	int B = *(int*)b;
	return A-B;
}

// Use 'mpirun -np 20 --oversubscribe ./pipeline_sort' to run the application with more processes
int main(int argc, char * argv[]) {
	int rank;
	int nProcesses;
	MPI_Init(&argc, &argv);

	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &nProcesses);
	printf("Hello from %i/%i\n", rank, nProcesses);
	
	N = nProcesses - 1;

	if (rank == 0) { // This code is run by a single process
		int intialValue = -1;
		int sorted = 0;
		int aux;
		int *v = (int*)malloc(sizeof(int) * (nProcesses - 1));
		int *vQSort = (int*)malloc(sizeof(int) * (nProcesses - 1));
		int i, val;

		// generate the vector v with random values
		// DO NOT MODIFY
		srandom(42);
		for(i = 0; i < nProcesses - 1; i++)
			v[i] = random() % 200;
		displayVector(v);

		// make copy to check it against qsort
		// DO NOT MODIFY
		for(i = 0; i < nProcesses - 1; i++)
			vQSort[i] = v[i];
		qsort(vQSort, nProcesses - 1, sizeof(int), cmp);

		// TODO sort the vector v

		for (i = 1 ; i < N ; ++i) {
			if (v[0] > v[i]) {
				MPI_Send(v, 1, MPI_INT, rank + 1, MPI_TAG_UB, MPI_COMM_WORLD);
				v[0] = v[i];
			} else {
				MPI_Send(&v[i], 1, MPI_INT, rank + 1, MPI_TAG_UB, MPI_COMM_WORLD);
			}
		}

		for (i = 1; i < N; ++i){
			MPI_Recv(&v[i], 1, MPI_INT, i, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		}

		displayVector(v);
		compareVectors(v, vQSort);
	} else {
		// TODO sort the vector v
		int recv, actual = -1;
		for (int i = rank ; i < N ; ++i) {
			MPI_Recv(&recv, 1, MPI_INT, rank - 1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			if (actual == -1) {
				actual = recv;
			} else {
				if (actual > recv) {
					int aux = recv;
					recv = actual;
					actual = aux;
				}
				if (rank < nProcesses) {
					MPI_Send(&recv, 1, MPI_INT, rank + 1, MPI_TAG_UB, MPI_COMM_WORLD);
				}
			}
		}
		MPI_Send(&actual, 1, MPI_INT, 0, MPI_TAG_UB, MPI_COMM_WORLD);
	}

	MPI_Finalize();
	return 0;
}
