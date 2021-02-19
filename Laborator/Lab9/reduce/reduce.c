#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#define MASTER 0

int main (int argc, char *argv[])
{
    int procs, rank;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &procs);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int value = rank;
    int current_sum = 0;

    for (int i = 2; i <= procs; i *= 2) {
        // TODO
        if ((rank % i) == 0) {
            MPI_Recv(&current_sum, 1, MPI_INT, rank + (i >> 1), MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            value += current_sum;
        } else if ((rank % (i >> 1)) == 0) {
            MPI_Send(&value, 1, MPI_INT, rank - (i >> 1), MPI_TAG_UB, MPI_COMM_WORLD);
        }
    }

    if (rank == MASTER) {
        printf("Result = %d\n", value);
    }

    MPI_Finalize();
}

