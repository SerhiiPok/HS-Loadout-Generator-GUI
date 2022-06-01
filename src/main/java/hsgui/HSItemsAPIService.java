package hsgui;

import HuntShowdownLib.IO.XMLItemReader;
import HuntShowdownLib.InGameTypes.GameItems.IItem;
import HuntShowdownLib.UtilityTypes.ItemCollection;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HSItemsAPIService extends Service<ItemCollection<IItem>> {

    private static final String serviceURL = "http://h2970256.stratoserver.net:8080/hs-loadout-gen/get-all-items";

    private String requestItems() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serviceURL))
                .GET()
                .build();
        HttpResponse<String> itemsXMLResponse = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        return itemsXMLResponse.body();
    }

    @Override
    protected Task<ItemCollection<IItem>> createTask() {

        return new Task<ItemCollection<IItem>>() {

            @Override
            protected ItemCollection<IItem> call() throws Exception {
                String itemsXML = requestItems();
                ItemCollection<IItem> items = XMLItemReader.createFromXMLString(itemsXML).readItems();
                return items;
            }
        };
    }
}
