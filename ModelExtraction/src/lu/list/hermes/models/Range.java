package lu.list.hermes.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Range")
public class Range {

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
    @JoinColumn(name="IDmr")
    private ModelRelation modelRelation;
	public ModelRelation getmodelRelation() {
		return this.modelRelation;
	}
	public void setmodelRelation(ModelRelation mr)
	{
		this.modelRelation = mr;
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
