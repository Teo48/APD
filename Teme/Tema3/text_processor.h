#ifndef __TEXT_PROCESSOR_H__
#define __TEXT_PROCESSOR_H__

static auto x = [](){
	std::ios::sync_with_stdio(false); 
	std::cin.tie(0); 
	std::cout.tie(0); 
	return nullptr;
};

#define MASTER (0)
#define MASTER_THREADS (4)
#define MAX_LINES_PER_THREAD (20)

#define HORROR (0)
#define COMEDY (1)
#define FANTASY (2)
#define SCIENCE_FICTION (3)

static std::unordered_map<std::string, int> paragraph_id = {
	{"horror", 0},
	{"comedy", 1},
	{"fantasy", 2},
	{"science-fiction", 3}
};

/*
    Credits: https://www.geeksforgeeks.org/tokenizing-a-string-cpp/
*/
static inline std::vector<std::string> tokenize(const std::string &s, const char delim) {
	std::vector<std::string> tokens;
	std::stringstream tokenizer(s);
	std::string aux = "";
	while (std::getline(tokenizer, aux, delim)) {
		tokens.push_back(aux);
	}
	return tokens;
}

static inline bool is_vowel(const char c) {
	if (c == 'a' || c == 'A' ||
		c == 'e' || c == 'E' ||
		c == 'i' || c == 'I' ||
		c == 'o' || c == 'O' ||
		c == 'u' || c == 'U') {
			return true;
		}

	return false;
}

void worker_foo(const std::string file_name, const int thread_id);
void process_text(const int id, const int genre, const int no_lines, const int no_threads);
void receive(const int genre);

#endif // __TEXT_PROCESSOR_H__