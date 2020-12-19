#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define ROOT 0

int main (int argc, char *argv[])
{
    int  numtasks, rank;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);
    
    // Checks the number of processes allowed.
    if (numtasks != 2) {
        printf("Wrong number of processes. Only 2 allowed!\n");
        MPI_Finalize();
        return 0;
    }

    // How many numbers will be sent.
    int send_numbers = 10;

    srand((unsigned int) time(NULL));

    if (rank == 0) {

        // Generate the random numbers.
        // Generate the random tags.
        // Sends the numbers with the tags to the second process.
        for (int i = 0 ; i < send_numbers ; ++i) {
            int rand_num = rand() % 1000;
            int tag = rand() % 1000;
            MPI_Send(&rand_num, 1, MPI_INT, 1, tag, MPI_COMM_WORLD);
            printf("Sent number %d with tag %d\n", rand_num, tag);
        }
        printf("------------------------------------\n");
    } else {

        // Receives the information from the first process.
        // Prints the numbers with their corresponding tags.
        MPI_Status status;
        for (int i = 0, recv_num ; i < send_numbers ; ++i) {
            MPI_Recv(&recv_num, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
            printf("Process with id %d sent %d with tag %d\n", status.MPI_SOURCE, recv_num, status.MPI_TAG);
        }
    }

    MPI_Finalize();

}

