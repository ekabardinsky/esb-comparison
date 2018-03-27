package ru.ekabardinsky.magister.camel.spring.boot.comparison.processor;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by ekabardinsky on 23.03.2018.
 */
public class JdbcAssembleQueryProcessor implements Processor {
    @Autowired
    private Gson gson;

    @Override
    public void process(Exchange exchange) throws Exception {
        HashMap body = gson.fromJson(exchange.getIn().getBody(String.class), HashMap.class);

        int columnsCount = Double.valueOf(body.get("columnsCount").toString()).intValue();
        int rowsCount = Double.valueOf(body.get("rowsCount").toString()).intValue();
        String serializeType = body.get("serializeType").toString();

        StringBuilder builder = new StringBuilder("select ");

        for (int i = 1; i <= columnsCount; i++) {
            builder.append("column_").append(i).append(i != columnsCount ?  "," : "");
        }

        builder.append(" from mock_data limit ").append(rowsCount);

        exchange.setProperty("serializeType", serializeType);
        exchange.getOut().setBody(builder.toString());
    }
}
