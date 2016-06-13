package lu.list.hermes.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Document")

   public class Document {
	@Id
    @GeneratedValue
    @Column(name = "IdDoc")
	private long IdDoc;
    
    public long getIdDoc(){
        return IdDoc;
    }	
    @Column(name = "DocText", length = 100000)

    private String DocText;
    @Column(name = "urid")

	private String urid;
	
    @ManyToOne
    @JoinColumn(name="IDc")
    private Corpus corpus;
	public Corpus getCorpus() {
		return this.corpus;
	}
	
	@OneToMany(fetch = FetchType.EAGER,mappedBy="document")
    private Set<Relation> Relations;

	public Set<Relation> getRelations() {
		return this.Relations;
	}

	public void setRelations(Set<Relation> Relations) {
		this.Relations = Relations;
	}
    
	@OneToMany(fetch = FetchType.EAGER,mappedBy="document")
    private Set<Annotation> Annotations;

	public Set<Annotation> getAnnotations() {
		return this.Annotations;
	}

	public void setAnnotations(Set<Annotation> Annotations) {
		this.Annotations = Annotations;
	}
	
	
	public void setDocText(String text)
	{
		this.DocText = text;
	}
	
	public String getDocText()
	{
		return DocText;
	}
	public void seturid(String uri)
	{
		this.urid = uri;
	}
	 public String geturid ()
	 {
		 return urid;
	 }
	
	public void setCorpus(Corpus c)
	{
		this.corpus = c ;
	}
	
	
	
	
	

}
