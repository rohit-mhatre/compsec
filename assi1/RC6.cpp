#include "RC6.hpp"

RC6::RC6(unsigned int W, unsigned int R, unsigned int B)
    : w(W), r(R), b(B), log_w(static_cast<unsigned int>(log2(w))), modulo(static_cast<int64_t>(pow(2, w))), S(new unsigned int[2 * r + 4]) {}

void RC6::rc_constraints(const unsigned int &w, unsigned int &p, unsigned int &q) {
    p = static_cast<unsigned int>(ceil(((M_E - 2) * pow(2, w))));
    q = static_cast<unsigned int>((1.618033988749895 - 1) * pow(2, w)); // Golden Ratio
}

int RC6::left_rot(unsigned int a, unsigned int b, unsigned int w) {
    b <<= w - log_w;
    b >>= w - log_w;
    return (a << b) | (a >> (w - b));
}

int RC6::right_rot(unsigned int a, unsigned int b, unsigned int w) {
    b <<= w - log_w;
    b >>= w - log_w;
    return (a >> b) | (a << (w - b));
}

std::string RC6::little_endian(std::string str) {
    std::string endian;
    if (str.length() % 2 == 1)
        str = "0" + str;
    for (auto r_it = str.rbegin(); r_it != str.rend(); r_it += 2) {
        endian.push_back(*(r_it + 1));
        endian.push_back(*r_it);
    }
    return endian;
}

std::string RC6::hex_to_string(unsigned int A, unsigned int B, unsigned int C, unsigned int D) {
    std::stringstream ss;
    std::string strA, strB, strC, strD, result;

    ss << std::setfill('0') << std::setw(4) << std::hex << A;
    strA = little_endian(ss.str());
    ss.str("");

    ss << std::setfill('0') << std::setw(4) << std::hex << B;
    strB = little_endian(ss.str());
    ss.str("");

    ss << std::setfill('0') << std::setw(4) << std::hex << C;
    strC = little_endian(ss.str());
    ss.str("");

    ss << std::setfill('0') << std::setw(4) << std::hex << D;
    strD = little_endian(ss.str());
    ss.str("");

    result = strA + strB + strC + strD;
    return result;
}

void RC6::key_schedule(std::string key) {
    const unsigned int w_bytes = static_cast<unsigned int>(ceil(static_cast<float>(w) / 8));
    const unsigned int c = static_cast<unsigned int>(ceil(static_cast<float>(b) / w_bytes));

    unsigned int p, q;
    rc_constraints(w, p, q);

    L = new unsigned int[c];
    for (int i = 0; i < c; i++) {
        L[i] = strtoul(little_endian(key.substr(w_bytes * 2 * i, w_bytes * 2)).c_str(), NULL, 16);
    }

    S[0] = p;
    for (int i = 1; i <= (2 * r + 3); i++) {
        S[i] = (S[i - 1] + q) % modulo;
    }

    unsigned int A = 0, B = 0, i = 0, j = 0;
    int v = 3 * std::max(c, (2 * r + 4));
    for (int s = 1; s <= v; s++) {
        A = S[i] = left_rot((S[i] + A + B) % modulo, 3, w);
        B = L[j] = left_rot((L[j] + A + B) % modulo, (A + B), w);
        i = (i + 1) % (2 * r + 4);
        j = (j + 1) % c;
    }
}

std::string RC6::encrypt(const std::string &text) {
    std::string result;
    unsigned int A, B, C, D;
    A = strtoul(little_endian(text.substr(0, 8)).c_str(), NULL, 16);
    B = strtoul(little_endian(text.substr(8, 8)).c_str(), NULL, 16);
    C = strtoul(little_endian(text.substr(16, 8)).c_str(), NULL, 16);
    D = strtoul(little_endian(text.substr(24, 8)).c_str(), NULL, 16);
    // Encryption Algorithm
    result = hex_to_string(A, B, C, D);
    return result;
}

std::string RC6::decrypt(const std::string &text) {
    std::string result;
    unsigned int A, B, C, D;
    A = strtoul(little_endian(text.substr(0, 8)).c_str(), NULL, 16);
    B = strtoul(little_endian(text.substr(8, 8)).c_str(), NULL, 16);
    C = strtoul(little_endian(text.substr(16, 8)).c_str(), NULL, 16);
    D = strtoul(little_endian(text.substr(24, 8)).c_str(), NULL, 16);
    // Decryption Algorithm
    result = hex_to_string(A, B, C, D);
    return result;
}

std::string RC6::run(const std::string &mode, const std::string &text, const std::string &key) {
    std::string result;
    key_schedule(key);
    if (mode.compare(0, strlen("Encryption"), "Encryption") == 0) {
        result = encrypt(text);
        result = result + " ";
    } else if (mode.compare(0, strlen("Decryption"), "Decryption") == 0) {
        result = decrypt(text);
        result = result + " ";
    }
        std::stringstream formatted_result;
    for (size_t i = 0; i < result.size(); i += 2) {
        formatted_result << result.substr(i, 2); // Extract two characters
        if (i + 2 < result.size()) {
            formatted_result << " "; // Insert space after every two characters except the last pair
        }
    }
    return formatted_result.str();
}

RC6::~RC6() {
    delete S;
}
