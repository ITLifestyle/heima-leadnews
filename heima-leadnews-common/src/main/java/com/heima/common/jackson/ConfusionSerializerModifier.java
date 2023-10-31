package com.heima.common.jackson;

import com.heima.model.common.annotation.IdEncrypt;
import org.apache.htrace.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import org.apache.htrace.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.ArrayList;
import java.util.List;

public class ConfusionSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        List<BeanPropertyWriter> newWriter = new ArrayList<>();
        for(BeanPropertyWriter writer : beanProperties){
            String name = writer.getType().getTypeName();
            if(null == writer.getAnnotation(IdEncrypt.class) && !writer.getName().equalsIgnoreCase("id")){
                newWriter.add(writer);
            } else {
                writer.assignSerializer(new ConfusionSerializer());
                newWriter.add(writer);
            }
        }
        return newWriter;
    }
}