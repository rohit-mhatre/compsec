#ifndef HEADER_HPP__
#define HEADER_HPP__

#include <iostream>
#include <string>
#include <cstdint>
#include <cstring>
#include <fstream>
#include <algorithm>
#include <cmath> // Include cmath for log2, pow, ceil
#include <iomanip> // Include iomanip for setfill, setw
#include <sstream>  


class RC6 {
private:
    unsigned int w, r, b, log_w;
    int64_t modulo;
    std::string mode, text, key;
    unsigned int *S;
    unsigned int *L;

    void rc_constraints(const unsigned int &, unsigned int &, unsigned int &);
    void key_schedule(std::string key);
    std::string encrypt(const std::string &);
    std::string decrypt(const std::string &);
    int left_rot(unsigned int, unsigned int, unsigned int);
    int right_rot(unsigned int, unsigned int, unsigned int);
    std::string little_endian(std::string);
    std::string hex_to_string(unsigned int A, unsigned int B, unsigned int C, unsigned int D);

public:
    RC6(unsigned int W = 32, unsigned int R = 20, unsigned int B = 16);
    std::string run(const std::string &, const std::string &, const std::string &);
    ~RC6();
};

#endif
