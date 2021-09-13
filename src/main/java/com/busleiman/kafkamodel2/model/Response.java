package com.busleiman.kafkamodel2.model;


import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Response implements Serializable {

private String details;
private boolean processedCorrectly;
}
