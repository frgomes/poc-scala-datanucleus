package model;

import javax.jdo.annotations.*;
import java.sql.Timestamp;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Indices({
        @Index(name="Intraday_idx_timestamp", members={"timestamp"})
})
public class Intraday {

    private static final long serialVersionUID = 1L;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public Identifier id;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public Timestamp timestamp;

    /** price as fixed point arithmetic 10E6 */
    @Persistent(nullValue=NullValue.EXCEPTION)
    public long price;

    /** high price as fixed point arithmetic 10E6 */
    @Persistent(nullValue=NullValue.EXCEPTION)
    public long high;

    /** low price as fixed point arithmetic 10E6 */
    @Persistent(nullValue=NullValue.EXCEPTION)
    public long low;

    /** variation price as fixed point arithmetic 10E6 */
    @Persistent(nullValue=NullValue.EXCEPTION)
    public long var;

    /** variation percentage price as fixed point arithmetic 10E6 */
    @Persistent(nullValue=NullValue.EXCEPTION)
    public long varpct;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public long volume;


    public Intraday(Identifier id, Timestamp timestamp, long price, long high, long low, long var, long varpct, long volume) {
        this.id = id;
        this.timestamp = timestamp;
        this.price = price;
        this.high = high;
        this.low = low;
        this.var = var;
        this.varpct = varpct;
        this.volume = volume;
    }
}
