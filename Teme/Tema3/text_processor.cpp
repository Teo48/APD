#include <bits/stdc++.h>
#include <fstream>
#include <unistd.h>
#include <mpi.h>
#include "text_processor.h"

std::vector<std::vector<std::string>> paragraphs(MASTER_THREADS, std::vector<std::string>());
std::vector<std::vector<std::string>> paragraphs_to_process(MASTER_THREADS, std::vector<std::string>());

std::vector<std::string> paragraph_order;
int current_processed_lines[4] = {0, 0, 0, 0};

pthread_barrier_t process_text_barrier;

/*
    Functia folosita de thread-ul din worker responsabil pentru receptionarea
    paragrafelor de la worker. Se primeste dimensiunea paragrafului si
    continutul efectiv pana la primirea mesajului end ce marcheaza terminarea
    fisierului de procesat, se calculeaza numarul de linii si numarul de 
    thread-uri ce trebuiesc pornite pentru a procesa textul(Ma asigur ca numarul
    de thread-uri nu depaseste numarul de core-uri disponibile pe sistem - 1).

    @param type - tipul paragrafului primit
*/

void receive(const int type) {
	while (true) {
		int buffer_size;
		MPI_Recv(&buffer_size, 1, MPI_INT, MASTER, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		char buffer[buffer_size];
		memset(buffer, 0, sizeof(buffer));
		MPI_Recv(&buffer, buffer_size, MPI_CHAR, MASTER, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		std::string temp(buffer, buffer_size);

		if (temp == "end") {
			break;
		}
		
		paragraphs_to_process[type - 1] = tokenize(temp, '\n');
		int line_counter = paragraphs_to_process[type - 1].size();
		int no_threads = (line_counter % MAX_LINES_PER_THREAD) == 0 ? 
                        line_counter / MAX_LINES_PER_THREAD : line_counter / MAX_LINES_PER_THREAD + 1;
		no_threads = std::min((int)std::thread::hardware_concurrency() - 1, no_threads);

		std::vector<std::thread> threads(no_threads);
		pthread_barrier_init(&process_text_barrier, NULL, no_threads);
		
		for (int i = 0; i < no_threads; ++i) {
			threads[i] = std::thread(process_text, i, type, line_counter, no_threads);
		}

		for (auto &i : threads) {
			i.join();
		}
	}
}

/*
    Functia folosita de cele 4 thread-uri din MASTER. Fiecare
    thread citeste din fisier doar paragrafele destinate lui.
    Dupa terminarea citirii sunt paragrafele trimise pe rand catre 
    fiecare worker si se asteapta receptionarea celor procesate.

    @param file_name Numele fisierului din care se citeste
    @param thread_id Id-ul thread-ului pornit de MASTER
*/
void worker_foo(const std::string file_name, const int thread_id) {
	std::ifstream fin(file_name);
	std::vector<std::string> current_thread_paragraphs;
	std::string current_paragraph = "";
	std::string line = "";
	
	while (!fin.eof()) {
		std::getline(fin, line);
		if (line == "horror" || line == "comedy" || line == "fantasy" || line == "science-fiction") {
			if (thread_id == 2) {
				paragraph_order.emplace_back(line);
			}
			if (thread_id == paragraph_id[line]) {
				while (std::getline(fin, line)) {
					if (line == "") {
						break;
					}
					current_paragraph += line;
					current_paragraph += '\n';
				}
				if (current_paragraph != "") {
					current_thread_paragraphs.emplace_back(current_paragraph);
				}
				current_paragraph = "";
			}
		}
	}

	for (const auto &it : current_thread_paragraphs) {
		int size = it.size();
		MPI_Send(&size, 1, MPI_INT, thread_id + 1, MPI_TAG_UB, MPI_COMM_WORLD);
		MPI_Send(it.c_str(), it.size(), MPI_CHAR, thread_id + 1, MPI_TAG_UB, MPI_COMM_WORLD);
		int buffer_size;
		MPI_Recv(&buffer_size, 1, MPI_INT, thread_id + 1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		char buffer[buffer_size];
		memset(buffer, 0, sizeof(buffer));
		MPI_Recv(&buffer, buffer_size, MPI_CHAR, thread_id + 1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		paragraphs[thread_id].emplace_back(std::string(buffer, buffer_size));
	}
	
	std::string e = "end";
	int size = e.size();
	MPI_Send(&size, 1, MPI_INT, thread_id + 1, MPI_TAG_UB, MPI_COMM_WORLD);
	MPI_Send(e.c_str(), e.size(), MPI_CHAR, thread_id + 1, MPI_TAG_UB, MPI_COMM_WORLD);
}

/*  
    Functia folosita de catre cele P - 1 thread-uri responsabile de procesarea
    paragrafelor.

    @param id ID-ul thread-ului
    @param type Tipul paragrafului
    @param no_lines Numarul de linii ale unui paragraf
    @param no_thread Numarul de threaduri pornite pentru paragarful curent
*/
void process_text(const int id, const int type, const int no_lines, const int no_threads) {
	int start, end;
	
	while (current_processed_lines[type - 1] < no_lines) {
		start = id * MAX_LINES_PER_THREAD + current_processed_lines[type - 1];
		end = std::min((id + 1) * MAX_LINES_PER_THREAD + current_processed_lines[type - 1], no_lines);
		if (start < no_lines) {
			for (int i = start; i < end; ++i) {
				switch (type - 1) {
					case HORROR: {
						std::vector<std::string> temp = tokenize(paragraphs_to_process[type - 1][i], ' ');
						std::string aux = "";

						for (int j = 0; j < temp.size(); ++j) {
							for (int k = 0; k < temp[j].size(); ++k) {
								if (!is_vowel(temp[j][k]) && isalpha(temp[j][k])) {
									aux += temp[j][k];
									if (temp[j][k] >= 'A' && temp[j][k] <= 'Z') {
										temp[j][k] += 32;
									}
									
								}
								aux += temp[j][k];
							}
							aux += " ";
						}
						paragraphs_to_process[type - 1][i] = aux;
                        break;
					}
					
					case COMEDY:  {
						std::vector<std::string> temp = tokenize(paragraphs_to_process[type - 1][i], ' ');
						std::string aux = "";

						for (int j = 0; j < temp.size(); ++j) {
							for (int k = 0; k < temp[j].size(); ++k) {
								if (((k + 1) & 1) == 0) {
									if (temp[j][k] >= 'a' && temp[j][k] <= 'z') {
										temp[j][k] -= 32;
									}
								}
								aux += temp[j][k];
							}
							aux += " ";
						}
						paragraphs_to_process[type - 1][i] = aux;
                        break;
					} 
					
					case FANTASY: {
						std::vector<std::string> temp = tokenize(paragraphs_to_process[type - 1][i], ' ');
						std::string aux = "";

						for (int j = 0; j < temp.size(); ++j) {
							if (temp[j][0] >= 'a' && temp[j][0] <= 'z') {
								temp[j][0] -= 32;
							}
							aux += temp[j][0];
							aux += temp[j].substr(1, temp[j].size());
							aux += " ";
						}
						paragraphs_to_process[type - 1][i] = aux;
                        break;
					}
					
					case SCIENCE_FICTION: {
						std::vector<std::string> temp = tokenize(paragraphs_to_process[type - 1][i], ' ');
						std::string aux = "";
						
						for (int j = 0; j < temp.size(); ++j) {
							if (temp.size() > 6 && (j + 1) % 7 == 0) {
								std::reverse(temp[j].begin(), temp[j].end());
							}

							aux += temp[j];
							aux += " ";
						}
						paragraphs_to_process[type - 1][i] = aux;
                        break;
					} 
					
					default: break;
				}
			}
        }

		pthread_barrier_wait(&process_text_barrier);
		if (id == 0) {
			current_processed_lines[type - 1] += MAX_LINES_PER_THREAD * no_threads;
		}
		pthread_barrier_wait(&process_text_barrier);
	}

	pthread_barrier_wait(&process_text_barrier);

	if (id == no_threads - 1) {
		std::string temp;
		for (const auto &it : paragraphs_to_process[type - 1]) {
			temp += it;
			temp += "\n";
		}
		int size = temp.size();
		MPI_Send(&size, 1, MPI_INT, MASTER, MPI_TAG_UB, MPI_COMM_WORLD);
		MPI_Send(temp.c_str(), temp.size(), MPI_CHAR, MASTER, MPI_TAG_UB, MPI_COMM_WORLD);
		current_processed_lines[type - 1] = 0;
	}
}

int main(int argc, char **argv) {
	int provided;
	MPI_Init_thread(&argc, &argv, MPI_THREAD_MULTIPLE, &provided);
	int numtasks, rank;
	MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);

	if (rank == MASTER) {
		std::vector<std::thread> threads(MASTER_THREADS);
        std::string file_name(argv[1]);
		for (int i = 0; i < MASTER_THREADS; ++i) {
			threads[i] = std::thread(worker_foo, file_name, i);
		}

		for (auto &i : threads) {
			i.join();
		}

        std::unordered_map<std::string, int> p_counter {
			{"horror", 0},
			{"comedy", 0},
			{"fantasy", 0},
			{"science-fiction", 0}
		};

		std::ofstream fout(file_name.substr(0, file_name.size() - 4) + ".out");
		for (int i = 0; i < paragraph_order.size(); ++i) {
			fout << paragraph_order[i] << '\n';
			fout << paragraphs[paragraph_id[paragraph_order[i]]][p_counter[paragraph_order[i]]++];
			fout << '\n';
		}
	} else {
		std::thread t = std::thread(receive, rank);
		t.join();
	}

	MPI_Finalize();
    return 0;
}