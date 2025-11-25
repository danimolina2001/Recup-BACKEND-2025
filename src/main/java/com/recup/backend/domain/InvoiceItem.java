package com.recup.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "INVOICE_ITEMS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_line_seq")
    @SequenceGenerator(name = "invoice_line_seq", sequenceName = "SEQ_INVOICE_LINE_ID", allocationSize = 1)
    @Column(name = "INVOICE_LINE_ID")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "INVOICE_ID")
    private Invoice invoice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "TRACK_ID")
    private Track track;

    @Column(name = "UNIT_PRICE", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "QUANTITY")
    private Integer quantity;
}
