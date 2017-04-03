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

package be.i8c.wso2.msf4j.lora.Services;


import be.i8c.wso2.msf4j.lora.models.SensorRecord;
import be.i8c.wso2.msf4j.lora.models.SensorType;
import be.i8c.wso2.msf4j.lora.repositories.LoRaRepository;
import be.i8c.wso2.msf4j.lora.utils.LoRaJsonConvertor;

import java.util.Calendar;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class contains methods for http request
 *
 * @since 0.1-SNAPSHOT
 */
@Component
@Path("/service")
public class LoRaRestService 
{

    private static final Logger LOGGER = LogManager.getLogger(LoRaRestService.class);

    /**
     * The repository to write the object to and to get object from
     */
    @Autowired
    private LoRaRepository repo;

    /**
     * This method return a sensorRecord in xml format. Currently it only returns an example object.
     * @return sensorRecord
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    public SensorRecord getXml() {
        LOGGER.debug("get invoked");
        SensorRecord r = new SensorRecord("x", "x", "1", "x", Calendar.getInstance().getTimeInMillis(), SensorType.Light);
        r.setSensorValue(20.0);
        
        return r;
    }

    /**
     * This method return a sensorRecord in json format. Currently it only returns an example object.
     * @return sensorRecord
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public SensorRecord getJson() {
        LOGGER.debug("get invoked");
        SensorRecord r = new SensorRecord("x", "x", "1", "x", Calendar.getInstance().getTimeInMillis(), SensorType.Light);
        r.setSensorValue(20.0);

        return r;
    }

    /**
     * This method is used to insert the object received from post request of proximus-Enco into database.
     * @param o json object to be inserted.
     * @return code 200 when insertion succeed, code 500 when insertion fails.
     */
    @POST
    @Path("/")
    public Response post(Object o) 
    {
        LOGGER.debug("post invoked. data: " + o);
        SensorRecord r = LoRaJsonConvertor.getInstance().convert(o.toString());
        SensorRecord result = repo.save(r);
        if (result != null)
            return Response.ok().build();
        else
            return Response.serverError().build();
    }

    /**
     * This method is used to save dummy data's received from post request into database for testing purposes
     * @param sensorRecord
     * @return
     */
    @POST
    @Path("/testing")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(SensorRecord sensorRecord)
    {
        LOGGER.info("testing post invoked. data: " + sensorRecord.toString());
        if (sensorRecord.getType() == null)
        {
            LOGGER.warn("data is empty: ");
            return Response.serverError().build();
        }
        else
        {
            SensorRecord result = repo.save(sensorRecord);
            if (result != null)
                return Response.ok().build();
            else
                return Response.serverError().build();
        }
    }
}
