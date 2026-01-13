package com.ecommerce.sb_ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "payments")
@Data @AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "order")
@EqualsAndHashCode(exclude = "order")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JsonIgnore
    private Order order;

    @NotBlank
    @Size(min=4, message = "Payment method must be 4 characters")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgStatus;
    private String pgName;
    private String pgResponseMessage;

    public Payment(String paymentMethod,String pgPaymentId,String pgStatus,String pgResponseMessage,String pgName){
        this.paymentMethod=paymentMethod;
        this.pgPaymentId=pgPaymentId;
        this.pgStatus=pgStatus;
        this.pgResponseMessage=pgResponseMessage;
        this.pgName=pgName;
    }
}
