package pet.wifispots.data.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Application error.
 */
@Entity
public @Data class Error {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "username")
    private String user = "";
    @Lob
    private String exception;
    @Lob
    private String type;
    private String url;
}
