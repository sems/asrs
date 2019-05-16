#include "core.hpp"
#include "packet.hpp"
#include "communication.hpp"

Core::Core(Communication &c) : communication(c), logger(c), started(false){

}

void Core::pollProgramLoop(){
	
    // No packet is received if communication.receivePacket return nullptr
    Packet *packet = this->communication.receivePacket();
    if(packet != nullptr){
		logger.logInfo("received packet");

        // Find the appropriate command
        for(int i = 0; i * sizeof(CommandInfo) < COMMAND_COUNT * sizeof(CommandInfo); i++){
            CommandInfo commandInfo = this->commands[i];
            if(commandInfo.Id == packet->CommandId)
            {
                // Checking whether a long running command is already in progress 
                // hapends in the command itself, 
                // because beforehand it is unknown what the error format looks like.
                commandInfo.Command(*this, this->communication, *packet);
            }
        }
    }
	
}