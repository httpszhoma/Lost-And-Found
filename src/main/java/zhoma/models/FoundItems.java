package zhoma.models;


import jakarta.persistence.*;

@Entity
public class FoundItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
