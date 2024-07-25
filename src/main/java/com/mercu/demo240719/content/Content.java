package com.mercu.demo240719.content;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "ba_cont")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cont_id")
    @SequenceGenerator(name = "cont_id", sequenceName = "CONT_ID", allocationSize = 1)
    @Setter
    private Long id;

    @Column
    private String data1;

    @Column
    private String data2;

}