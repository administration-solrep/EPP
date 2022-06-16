package fr.dila.st.core.status;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.nuxeo.runtime.api.Framework;

public class KafkaStatus implements SolonStatusInfo {

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();

        if (Framework.isBooleanPropertyFalse("kafka.enabled")) {
            resultInfo.setDescription("Kafka n'est pas activé");
            return resultInfo;
        }

        String kafkaBootstrapServers = Framework.getProperty("kafka.bootstrap.servers");
        if (StringUtils.isEmpty(kafkaBootstrapServers)) {
            setKo(resultInfo, "kafka.bootstrap.servers n'est pas configuré");
            return resultInfo;
        }

        Properties properties = new Properties();
        properties.put("connections.max.idle.ms", 10000);
        properties.put("request.timeout.ms", 5000);
        for (String kafkaServer : Arrays.asList(kafkaBootstrapServers.split(","))) {
            properties.put("bootstrap.servers", kafkaServer);
            try (AdminClient client = KafkaAdminClient.create(properties)) {
                client.listTopics().names().get(); // throws exceptions in case of failure to get
                resultInfo.put(kafkaServer, ResultEnum.OK);
            } catch (Exception e) {
                setKo(resultInfo, null);
                resultInfo.put(kafkaServer, ResultEnum.KO + " - " + e.getMessage());
            }
        }

        return resultInfo;
    }
}
