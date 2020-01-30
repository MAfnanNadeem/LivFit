/*
 *  Created by Sumeet Kumar on 1/27/20 3:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 1:49 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.models;


import java.net.InetAddress;

public interface IDevice extends BaseModel {

    int getColorPalet();
    String getName();
    String getUid();
    ConnectionTypes getConnectionType();
    InetAddress getIp();
    String getIpToString();
    String getModelNumber();

}