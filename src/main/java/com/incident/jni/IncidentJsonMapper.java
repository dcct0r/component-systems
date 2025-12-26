package com.incident.jni;

import com.incident.model.Incident;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class IncidentJsonMapper {

    private static final Jsonb JSONB = JsonbBuilder.create();

    public static String toJson(Incident incident) {
        return JSONB.toJson(incident);
    }
}
