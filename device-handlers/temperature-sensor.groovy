/*
*
*  	Spark Core Temperature & Humidity Sensor
*
*  	Copyright 2016 Rich Zygler
*
*	INSTALLATION
*	------------
* 	1) 	Create a new device type (https://graph.api.smartthings.com/ide/devices)
*     	Name: Temperature Sensor
*     	Author: Rich Zygler
*     	Capabilities:
*         	Polling,
*			Relative Humidity Measurement,
*			Sensor,
*			Temperature Measurement
*
* 	2) 	Create a new device (https://graph.api.smartthings.com/device/list)
*     	Name: Your Choice
*     	Device Network Id: Your Choice
*     	Type: Spark Core Temperature Sensor (should be the last option)
*     	Location: Choose the correct location
*     	Hub/Group: Leave blank
*
* 	3) 	Update device preferences
*     	Click on the new device to see the details.
*     	Click the edit button next to Preferences
*     	Enter the Device ID and Access Token
*
*	4) 	Open the Mobile Application and add the newly created device,
*		click refresh to see the Temperature and Humidity values
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*		http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*/

preferences {
    input("deviceId", "text", title: "Device ID")
    input("token", "text", title: "Access Token")
}


metadata {
	definition (name: "Temperature Sensor", namespace: "rzygler/fishtanktemperature", author: "Rich Zygler") {
		capability "Polling"
		capability "Temperature Measurement"
        capability "Sensor"
	}

	simulator {

	}

	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2){
            state "temperature", label: '${currentValue}°', unit:"",
                   backgroundColors:[    
                    [value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
		}

        standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
            state "default", action:"polling.poll", icon:"st.secondary.refresh"
        }

        main "temperature"
		details(["temperature", "refresh"])
	}
}

// handle commands
def poll() {
	log.debug "Executing 'poll'"

    getTemperature()
	//getTempTest()
}

private getTempTest()
{
	sendEvent(name: "temperature", value: 72)
}

// Get the temperature & humidity
private getTemperature() {
    //Spark Core API Call
    
    def temperatureClosure = { response ->
	  	log.debug "Temeprature Request was successful, ${response.data}"

      	sendEvent(name: "temperature", value: response.data.result)
	}
	def tempFailed = { response -> 
    	log.debug "Temp failed ${response.data}"
        }
        
    def temperatureParams = [
  		uri: "https://api.particle.io/v1/devices/${deviceId}/tempf?access_token=${token}",
        success: temperatureClosure,
        failure: tempFailed
	]
	try {
    // log.debug(temperatureParams.uri)
    httpGet(temperatureParams)
	} catch (e) {
    	log.error "something went wrong: $e"
	}
}

