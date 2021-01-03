package gmuvvala.kafka.Demo1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerExample {
	private static Producer<Long, String> createProducer() {
		Properties props = new Properties();
		try(InputStream inputStream =  ProducerExample.class.getResourceAsStream("environment.properties")
			){
			// load the properties
			props.load(inputStream);        	   
			// read a value
			String url = props.getProperty("bootstrap.servers");

			// display the value
			System.out.println("Using kafka server: "+url);

		} catch (IOException e) {
			e.printStackTrace();
		} 	
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "ProducerExample");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "TId1");
        return new KafkaProducer<>(props);
    }
	
	
	public static void main ( String args[]) throws Exception {
		Producer<Long, String> kafkaProducer = createProducer();
		kafkaProducer.initTransactions();
		for(Long i=0l ; i<5 ; i++) {
			
			String TOPIC= "my-topic";
			final ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, i,  "test " + i);
			long time = System.currentTimeMillis();
			kafkaProducer.beginTransaction();
			RecordMetadata metadata = kafkaProducer.send(record).get();
			kafkaProducer.commitTransaction();
			long elapsedTime = System.currentTimeMillis() - time;
            System.out.printf("sent record(key=%s value=%s) " +
                            "meta(partition=%d, offset=%d) time=%d\n",
                    record.key(), record.value(), metadata.partition(),
                    metadata.offset(), elapsedTime);
			
			
		}
		
		kafkaProducer.close();
		
		
	
	}

}
