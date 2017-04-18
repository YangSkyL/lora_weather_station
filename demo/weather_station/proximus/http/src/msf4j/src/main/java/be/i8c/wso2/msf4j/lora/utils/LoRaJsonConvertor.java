/*
  * Copyright (c) 2017, i8c N.V. (Integr8 Consulting; http://www.i8c.be)
  * All Rights Reserved.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package be.i8c.wso2.msf4j.lora.utils;

import be.i8c.wso2.msf4j.lora.models.SensorRecord;
import be.i8c.wso2.msf4j.lora.models.SensorType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to convert json object into model class SensorRecord and vice versa.
 *
 * Note: It's necessary to use this class to convert the json object from Proximus,
 * because the structure of json object from Proximus is different per SensorType.
 *
 * @author yanglin
 */
public class LoRaJsonConvertor 
{
    private static LoRaJsonConvertor instance = new LoRaJsonConvertor();
    
    private static final Logger LOGGER = LogManager.getLogger(LoRaJsonConvertor.class);
    private Gson gson;
    
    private LoRaJsonConvertor()
    {
        gson = new Gson();
        
    }
    
    public static LoRaJsonConvertor getInstance()
    {
        return instance;
    }

    /**
     * This method is used to convert a json object forwarded from Proximus-enco into a SensorRecord object.
     * @param s json object to be converted.
     * @return a SensorRecord object
     */
    public SensorRecord convertFromProximus(String s)
    {
        LOGGER.info("converting json object");
        LOGGER.debug("object: " + s);
        SensorRecord r = gson.fromJson(s, SensorRecord.class);
        JsonObject jo = new JsonParser().parse(s).getAsJsonObject();
        SensorType sensorType = getTypeFromJSON(jo);
        r.setType(sensorType);
        r.setSensorValue(getValueFromJSON(jo, sensorType));
        LOGGER.info("json object successfully converted to " + r.simpleString());
        LOGGER.debug("converted Record object: " + r.toString());
        return r;
    }

    /**
     * This method is used to convert a SensorRecord object into json object in string format.
     * @param t SensorRecord object to be convert
     * @return json object in string format
     */
    public String convertToJsonString(SensorRecord t)
    {
        return this.gson.toJson(t, t.getClass());
    }

    /**
     * This method is used to found sensor value from a json object forwarded from Proximus
     * @param j json object received from Proximus-enco
     * @param s Type of sensor
     * @return value of sensor
     */
    public Double getValueFromJSON(JsonObject j, SensorType s)
    {
        LOGGER.info("getting value from JSONobject: " + j);
        LOGGER.debug("getting sensortype: [" + s + "] value from JSON: " + j);
        return Double.parseDouble(j.get(s.getValueString()).getAsString());
    }

    /**
     * This method is used to found sensor type from a json object forwarded from Proximus
     * @param j json object forwarded from Proximus
     * @return type of sensor
     */
    private SensorType getTypeFromJSON(JsonObject j)
    {
        LOGGER.info("getting sensortype from JSONobject: " + j);
        String desc = j.get("streamDescription").getAsString();
        LOGGER.debug("streamDescription: [" + desc + "]");
        SensorType sensorType = 
                Stream.of(SensorType.values())
                .filter(s -> this.compDesc(desc,s.getDesc()))
                .findFirst()
                .get();        
        LOGGER.debug("found sensortype : [" + sensorType + "]");
        return sensorType;
    }

    /**
     * This method is used to check if first string contains second string
     * @param desc first string
     * @param typeDesc second string
     * @return boolean
     */
    private boolean compDesc(String desc, String typeDesc)
    {
        LOGGER.debug("comparing [" + desc + "] to [" + typeDesc + "]");
        return desc.toLowerCase().contains(typeDesc);
    }
   
}