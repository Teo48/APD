#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

void *f(void *arg) {
	int id = *(int* )arg;
	
	//for (int i = 0 ; i < 100 ; ++i) {
	//	printf("Iteratia:%d Hello World din thread-ul %ld!\n", i, id);
	//}
    
	printf("Hello World din thread-ul %d!\n", id);
  	pthread_exit(NULL);
}

void *dummy_f(void *arg) {
	int id = *(int *)arg;
	
    printf("Hello World din thread-ul %d!\n", id);
  	
    pthread_exit(NULL);
}


int main(int argc, char *argv[]) {
    #define NUM_THREADS cores
	long cores = sysconf(_SC_NPROCESSORS_CONF);
	pthread_t threads[NUM_THREADS];
	int r;
    long id;
    void *status;
	
  	/* Task 2-3
       for (id = 0; id < NUM_THREADS; id++) {
		r = pthread_create(&threads[id], NULL, f, (void *)id);

		if (r) {
	  		printf("Eroare la crearea thread-ului %ld\n", id);
	  		exit(-1);
		}
  	}

  	for (id = 0; id < NUM_THREADS; id++) {
		r = pthread_join(threads[id], &status);

		if (r) {
	  		printf("Eroare la asteptarea thread-ului %ld\n", id);
	  		exit(-1);
		}
  	}
    */
    
    int thread_id[NUM_THREADS];
    for (int i = 0 ; i < NUM_THREADS; ++i) {
        thread_id[i] = i;
    }

    r = pthread_create(&threads[0], NULL, f, &thread_id[0]);
	
    if (r) {
        printf("Eroare la crearea thread-ului 0\n");
  		exit(-1);
	}
    
    r = pthread_create(&threads[1], NULL, dummy_f, &thread_id[1]);
	
    if (r) {
        printf("Eroare la crearea thread-ului 1\n");
  		exit(-1);
	}
	 
    pthread_join(threads[0], &status);
    pthread_join(threads[1], &status);
    pthread_exit(NULL);

    return 0;
}
