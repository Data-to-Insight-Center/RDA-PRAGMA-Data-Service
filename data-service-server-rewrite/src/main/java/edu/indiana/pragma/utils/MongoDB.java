/*
 *
 * Copyright 2018 The Trustees of Indiana University
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
 *
 *
 * @creator kunarath@iu.edu
 */
package edu.indiana.pragma.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

    static public MongoClient mongoClientInstance = null;
    public static String researchObjects = "PIDCollection";

    public static synchronized MongoClient getMongoClientInstance() {
        if (mongoClientInstance == null) {
            try {
                mongoClientInstance = new MongoClient(Constants.mongoHost, Constants.mongoPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mongoClientInstance;
    }

    static public MongoDatabase getServicesDB() {
        return getMongoClientInstance().getDatabase(Constants.pdtDbName);
    }

}
