package model;

import java.io.Reader;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;

import com.google.gson.Gson;



@PersistenceCapable
@Sequence(name="uuid", datastoreSequence="uuid", strategy=SequenceStrategy.NONCONTIGUOUS)
@Version(strategy=VersionStrategy.VERSION_NUMBER)
public abstract class AbstractModel implements Serializable, StoreCallback {

    private static final long serialVersionUID = 1L;

    @PrimaryKey
    @Persistent(nullValue=NullValue.EXCEPTION, valueStrategy=IdGeneratorStrategy.UUIDHEX, sequence="uuid")
    public String uuid;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public Timestamp created;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public Timestamp updated;

    @Override
    public void jdoPreStore() {
        Timestamp now = Timestamp.from(Instant.now());
        if(this.created == null) this.created = now;
        this.updated = now;
    }


    private static Gson gson = new Gson();

    public String toJSON() {
        return gson.toJson(this);
    }

    public <T> T fromJson(Class<T> klass, String string) {
        return gson.fromJson(string, klass);
    }

    public <T> T fromJson(Class<T> klass, Reader reader) {
        return gson.fromJson(reader, klass);
    }
}
