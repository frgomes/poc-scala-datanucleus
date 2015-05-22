package model;

import javax.jdo.annotations.*;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Sequence(name="id", datastoreSequence="id", strategy=SequenceStrategy.CONTIGUOUS)
@Uniques({
        @Unique(name="Identifier_idx_symbol", members={"symbol"})
})
public class Identifier extends AbstractModel {

    private static final long serialVersionUID = 1L;

    @Persistent(nullValue=NullValue.EXCEPTION, valueStrategy=IdGeneratorStrategy.INCREMENT, sequence="id")
    public int id;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public String symbol;

    @Persistent(nullValue=NullValue.EXCEPTION)
    public String name;

    // company name, full length
    @Persistent(nullValue=NullValue.EXCEPTION)
    public String company;

    // company name, abbreviated
    @Persistent(nullValue=NullValue.EXCEPTION)
    public String scompany;

    @Persistent(nullValue=NullValue.NONE)
    public String fk;


    public Identifier(String symbol, String name, String company) {
        this(symbol, name, company, company, null);
    }
    public Identifier(String symbol, String name, String company, String scompany) {
        this(symbol, name, company, scompany, null);
    }
    public Identifier(String symbol, String name, String company, String scompany, String fk) {
        this.symbol   = symbol;
        this.name     = name;
        this.company  = company;
        this.scompany = scompany;
        this.fk       = fk;
    }
}
