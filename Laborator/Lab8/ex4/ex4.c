#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define ROOT 3

int main (int argc, char *argv[])
{
    int  numtasks, rank, len;
    char hostname[MPI_MAX_PROCESSOR_NAME];

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);
    MPI_Get_processor_name(hostname, &len);

    int value;
    MPI_Status status;

    if (rank == ROOT) {

        // The ROOT process receives an element from any source.
        // Prints the element and the source. HINT: MPI_Status.
        for (int i = 0 ; i < 3 ; ++i) {
            MPI_Recv(&value, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
            printf("Worker with id %d has the number %d\n", status.MPI_SOURCE, value);
        }
    } else {

        // Generate a random number.
        srand((unsigned int)time(NULL));
        // O sa genereze 0 mereu pentru primul rank -> restul impartirii la 1 e mereu 0
        value = rand() % (rank * 50 + 1);
        int tag = rand() % (rank * 50 + 1);
        printf("Process [%d] send %d.\n", rank, value);

        // Sends the value to the ROOT process.
        MPI_Send(&value, 1, MPI_INT, ROOT, tag, MPI_COMM_WORLD);

    }

    MPI_Finalize();

}

