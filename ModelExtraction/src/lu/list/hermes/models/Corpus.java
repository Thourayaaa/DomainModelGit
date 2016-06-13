package lu.list.hermes.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Corpus")
public class Corpus {
	@Id
    @GeneratedValue
    @Column(name = "IDc")

	private long IDc;
    
    public long getIDc(){
        return IDc;
    }   
    @Column(name = "CorpusName")

    private String CorpusName;
    @Column(name = "path")

    private String path;
    
    @OneToMany(fetch = FetchType.EAGER,mappedBy="corpus")
    private Set<Document> Documents;

	public Set<Document> getDocuments() {
		return this.Documents;
	}

	public void setDocuments(Set<Document> Documents) {
		this.Documents = Documents;
	}
    
      public void setCorpusName(String cname)
    {
    	this.CorpusName = cname;
    	
    }
    
    public String getCorpusName ()
    
    {
    	return CorpusName;
    }
    
    public void setpath(String p)
    {
    	this.path = p;
    }
    public String getpath()
    
    {
    	return path;
    }
}
