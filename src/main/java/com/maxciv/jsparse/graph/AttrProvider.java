package com.maxciv.jsparse.graph;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.AttributeType;
import org.jgrapht.io.ComponentAttributeProvider;

import java.util.HashMap;
import java.util.Map;

public class AttrProvider implements ComponentAttributeProvider<VertexJSp> {

    @Override
    public Map<String, Attribute> getComponentAttributes(VertexJSp component) {
        Map<String, Attribute> map = new HashMap<>();
        map.put("shape", new Attribute() {
            @Override
            public String getValue() {
                return component.getShape().toString();
            }

            @Override
            public AttributeType getType() {
                return AttributeType.STRING;
            }
        });

        return map;
    }
}
