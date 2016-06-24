package lu.list.hermes.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "RelationRange")
public class RelationRange {

	@Id
    @GeneratedValue
    @Column(name = "iDran")
    private long iDran;
  
    public long getiDran(){
        return iDran;
    }
    @Column(name = "rangeURI", length = 100000)

	private String rangeURI;
    @ManyToOne
    @JoinColumn(name="IDrela")
    private ModelRelation modelrelation;
	public ModelRelation getmodelrelation() {
    		return this.modelrelation;
	}
	public void setmodelrelation(ModelRelation mr)
	{
		this.modelrelation = mr;
	}
    
  
    
    public void setrangeURI(String uri)
    {
    	this.rangeURI = uri;
    }
    
    public String getrangeURI()
    {
    	return rangeURI;
    }
    
}
