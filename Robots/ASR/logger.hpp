#ifndef LOGGER_HPP
#define LOGGER_HPP

#include "Arduino.h"

extern const char* ERROR_PREFIX;
extern const char* INFO_PREFIX;

class Communication;

class Logger
{
private:
    Communication &communication;
    void SendLogPacket(char *message); 
public:
    Logger(Communication &communication);
    void logInfo(const char *message);
    void logError(const char *errorMessage);
};

#endif