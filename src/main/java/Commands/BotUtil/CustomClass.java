package Commands.BotUtil;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class CustomClass implements Codec<CustomClass> {
	private int one;
	private String two;

	public CustomClass(){
		one = 0;
		two = "0";
	}

	private CustomClass(int one, String two){
		this.one = one;
		this.two = two;
	}

	public int getOne() {
		return one;
	}

	public void setOne(int one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	@Override
	public CustomClass decode(BsonReader reader, DecoderContext decoderContext) {
		one = reader.readInt32("one");
		two = reader.readString("two");
		return this;
	}

	@Override
	public void encode(BsonWriter writer, CustomClass value, EncoderContext encoderContext) {
		if(value != null) {
			writer.writeInt32("one", value.one);
			writer.writeString("two", value.two);
		}
	}

	@Override
	public Class<CustomClass> getEncoderClass() {
		return CustomClass.class;
	}
}
