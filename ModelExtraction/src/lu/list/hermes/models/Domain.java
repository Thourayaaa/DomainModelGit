package lu.list.hermes.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Domain")
public class Domain {

	@Id
    @GeneratedValue
    @Column(name = "iDdom")
    private long iDdom;
  
    public long getiDdom(){
        return iDdom;
    }
    @Column(name = "domainURI", length = 100000)

	private String domainURI;
    
  
    
    public void setdomainURI(String uri)
    {
    	this.domainURI = uri;
    }
    
    public String getdomainURI()
    {
    	return domainURI;
    }
    @ManyToOne
    @JoinColumn(name="IDr")
    private ModelRelation modelrelation;
	public ModelRelation getmodelrelation() {
		return this.modelrelation;
	}
	public void setmodelrelation(ModelRelation mr)
	{
		this.modelrelation = mr;
	}
}
