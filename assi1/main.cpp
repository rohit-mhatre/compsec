#include <iostream>
#include <fstream>
#include <cstring>
#include <algorithm>
#include "HEADER.hpp"

#define RC6_W 32
#define RC6_R 20

// Function to print usage instructions
void require(int argc, char *argv[]) {
    std::cout << "require: \n./run ./input.txt ./output.txt" << std::endl;
}

// Function to parse the input file for RC6 encryption/decryption
// Extracts mode, text, and user key
bool parseRC6(std::fstream &file, std::string &mode, std::string &text, std::string &userkey) {
    std::string extract;
    int linecount = 0;
    bool err = false;
    if (file.is_open()) {
        while (getline(file, extract) && !err) {
            switch (linecount++) {
                case 0:
                    if (extract.compare(0, strlen("Encryption"), "Encryption") == 0 ||
                        extract.compare(0, strlen("Decryption"), "Decryption") == 0)
                        mode = extract;
                    else
                        err = true;
                    break;
                case 1:
                    if (extract.find("plaintext: ") == 0 || extract.find("ciphertext: ") == 0)
                        text = extract.substr(strlen("plaintext: "));
                    else
                        err = true;
                    break;
                case 2:
                    if (extract.find("userkey: ") == 0)
                        userkey = extract.substr(strlen("userkey: "));
                    else
                        err = true;
                    break;
                default:
                    break;
            }
        }
    }
    return err;
}

// Function to determine the length of the encryption key in bytes
unsigned int keylength(const std::string &key) {
    return key.length() / 2;
}

int main(int argc, char *argv[]) {
    // Check if the correct number of arguments is provided
    if (argc != 3) {
        std::cout << "Incorrect number of arguments" << std::endl;
        require(argc, argv);
        return 0;
    }
    
    // Input File Raed
    std::fstream inputfile(argv[1], std::fstream::in);
    if (!inputfile.is_open()) {
        std::cout << "Failed to read input file" << std::endl;
        return 0;
    }

    // Output File Readd
    std::fstream outputfile(argv[2], std::fstream::out | std::fstream::trunc);
    if (!outputfile.is_open()) {
        std::cout << "Failed to write in output file" << std::endl;
        return 0;
    }

    // Parse the input file
    std::string mode, text, userkey;
    if (parseRC6(inputfile, mode, text, userkey) != 0) {
        std::cout << "Error parsing input file" << std::endl;
        return 0;
    }
        
    // Remove whitespace from text and user key
    text.erase(std::remove_if(text.begin(), text.end(), ::isspace), text.end());
    userkey.erase(std::remove_if(userkey.begin(), userkey.end(), ::isspace), userkey.end());

    // Initialize RC6 instance
    RC6 rc6(RC6_W, RC6_R, keylength(userkey));
    // Run RC6 encryption/decryption
    std::string result = rc6.run(mode, text, userkey);

    // Result Writing
    if (mode.compare(0, strlen("Encryption"), "Encryption") == 0)
        outputfile << "ciphertext: " << result << std::endl;
    else if (mode.compare(0, strlen("Decryption"), "Decryption") == 0)
        outputfile << "plaintext: " << result << std::endl;

    inputfile.close();
    outputfile.close();

    return 0;
}
