package home.greg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 10.10.15.
 * an object to store "readytosend" data
 */
public class Document {
    private List<String> text = new ArrayList<>();
    private MetaData metaData;

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public List<String> getText() {
        return text;
    }

    public void addText(String line) {
        text.add(line);
    }

    public void setText(List<String> text) {
        this.text = text;
    }

}
