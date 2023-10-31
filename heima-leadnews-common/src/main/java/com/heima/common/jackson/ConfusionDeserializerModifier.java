package com.heima.common.jackson;

import com.heima.model.common.annotation.IdEncrypt;
import org.apache.htrace.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import org.apache.htrace.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import org.apache.htrace.fasterxml.jackson.databind.deser.SettableBeanProperty;

import java.util.Iterator;

public class ConfusionDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public BeanDeserializerBuilder updateBuilder(final DeserializationConfig config, final BeanDescription beanDescription, final BeanDeserializerBuilder builder) {
        Iterator it = builder.getProperties();

        while (it.hasNext()) {
            SettableBeanProperty p = (SettableBeanProperty) it.next();
            if ((null != p.getAnnotation(IdEncrypt.class)||p.getName().equalsIgnoreCase("id"))) {
                JsonDeserializer<Object> current = p.getValueDeserializer();
                builder.addOrReplaceProperty(p.withValueDeserializer(new ConfusionDeserializer(p.getValueDeserializer(),p.getType())), true);
            }
        }
        return builder;
    }
}