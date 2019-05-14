#include "logger.hpp"
#include "packet.hpp"
#include "communication.hpp"

const char* INFO_PREFIX = "Info: ";
const char* ERROR_PREFIX = "Error: ";

Logger::Logger(Communication &communication) : communication(communication){
    
}

void Logger::logInfo(const char *message)
{
    // Concat error message with prefix
    const size_t len1 = strlen(INFO_PREFIX);
    const size_t len2 = strlen(message);
    char *result = malloc(len1 + len2 + 1); // +1 for the null-terminator
    // in real code you would check for errors in malloc here
    memcpy(result, INFO_PREFIX, len1);
    memcpy(result + len1, message, len2 + 1); // +1 to copy the null-terminator
    SendLogPacket(result);
    free(result);
}

void Logger::logError(const char *errorMessage)
{
    // Concat error message with prefix
    const size_t len1 = strlen(ERROR_PREFIX);
    const size_t len2 = strlen(errorMessage);
    char *result = malloc(len1 + len2 + 1); // +1 for the null-terminator
    // in real code you would check for errors in malloc here
    memcpy(result, ERROR_PREFIX, len1);
    memcpy(result + len1, errorMessage, len2 + 1); // +1 to copy the null-terminator
    
    SendLogPacket(result);
    free(result);
}

void Logger::SendLogPacket(char *message){
    Packet *packet = Packet::createLogPacket(message);
	
    this->communication.sendPacket(packet);
    delete packet;
}