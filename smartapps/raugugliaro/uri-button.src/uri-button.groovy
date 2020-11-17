/**
 *  URIButton
 *
 *  Copyright 2020 Richard Augugliaro
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
definition(
    name: "URIButton",
    namespace: "raugugliaro",
    author: "Richard Augugliaro",
    description: "From GitHub ruricu URI Button to link to web hook",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png") {
    appSetting "camer"
    appSetting "action"
}


def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}
*/
preferences {
	section("External Access"){
		input "external_uri", "text", title: "External URI", required: false
	}
    
	section("Internal Access"){
		input "internal_ip", "text", title: "Internal IP", required: false
		input "internal_port", "text", title: "Internal Port (if not 80)", required: false
		input "internal_path", "text", title: "Internal Path (/blah?q=this)", required: false
	}
} 


	definition (name: "URI Button", namespace: "raugugliaro", author: "Richard Augugliro") {
		capability "Actuator" 
 		capability "Switch" 
 		capability "Momentary" 
 		capability "Sensor"
        capability "Button"
	}


	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Push', action: "momentary.push", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'Push', action: "momentary.push", backgroundColor: "#53a7c0"
		}
		main "switch"
		details "switch"
	}

// parse events into attributes
def parse(String description) {
	log.debug(description)
}

// handle commands
def push() {
	log.debug "Executing 'push'"
    if (external_uri){
		// sendEvent(name: "switch", value: "on")
		// log.debug "Executing ON"

		def cmd = "${settings.external_uri}";

		log.debug "Sending request cmd[${cmd}]"

			httpGet(cmd) {resp ->
				if (resp.data) {
					log.info "${resp.data}"
				} 
			}
	}
	if (internal_path){
		def port
			if (internal_port){
				port = "${internal_port}"
			} else {
				port = 80
			}

		def result = new physicalgraph.device.HubAction(
				method: "GET",
				path: "${internal_path}",
				headers: [
				HOST: "${internal_ip}:${port}"
				]
				)
			sendHubCommand(result)
			log.debug "Executing ON" 
			log.debug result
	}
    sendEvent(name: "switch", value: "on", isStateChange: true, display: false)
	sendEvent(name: "switch", value: "off", isStateChange: true, display: false)
	sendEvent(name: "momentary", value: "pushed", isStateChange: true)
    sendEvent(name: "button", value: "pushed", isStateChange: true, data: [buttonNumber: 1])
}

def on() {
	push()
}

def off() {
	push()
}