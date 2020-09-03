package software.bernie.geckolib.geo.raw.pojo;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;

@JsonDeserialize(using = UvUnion.Deserializer.class)
@JsonSerialize(using = UvUnion.Serializer.class)
public class UvUnion {
    public double[] boxUVCoords;
    public UvFaces faceUV;
    public boolean isBoxUV;

    static class Deserializer extends JsonDeserializer<UvUnion> {
        @Override
        public UvUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            UvUnion value = new UvUnion();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case START_ARRAY:
                    value.boxUVCoords = jsonParser.readValueAs(double[].class);
                    value.isBoxUV = true;
                    break;
                case START_OBJECT:
                    value.faceUV = jsonParser.readValueAs(UvFaces.class);
                    value.isBoxUV = false;
                    break;
                default: throw new IOException("Cannot deserialize UvUnion");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<UvUnion> {
        @Override
        public void serialize(UvUnion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.boxUVCoords != null) {
                jsonGenerator.writeObject(obj.boxUVCoords);
                return;
            }
            if (obj.faceUV != null) {
                jsonGenerator.writeObject(obj.faceUV);
                return;
            }
            jsonGenerator.writeNull();
        }
    }
}
