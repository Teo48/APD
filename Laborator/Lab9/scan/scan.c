#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

#define MASTER 0

int main (int argc, char *argv[])
{
    int procs, rank;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &procs);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int value = rank;
    int recv_value;

    for (int i = 1; i < procs; i *= 2) {
        // TODO
        if (rank + i < procs) {
            MPI_Send(&value, 1, MPI_INT, rank + i, MPI_TAG_UB, MPI_COMM_WORLD);
        }
        
        if (rank - i >= 0) {
            MPI_Recv(&recv_value, 1, MPI_INT, rank - i, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            value += recv_value;
        }
    }

    printf("Process [%d] has result = %d\n", rank, value);

    MPI_Finalize();

}

