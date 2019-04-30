package com.huawei.mfg.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.mfg.bean.MfgEtlServiceKey;

import java.io.IOException;

public class TestMain {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        MfgEtlServiceKeyExtended serviceKeyExtended = new MfgEtlServiceKeyExtended();
        serviceKeyExtended.setId(10);
        try {
            serviceKeyExtended.setId(100);
            Child child = new Child();
            System.err.println(objectMapper.writeValueAsString(child));
            System.err.println(objectMapper.writeValueAsString(serviceKeyExtended));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Super {
        private String firstName;
        private String lastName;
        private int age;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"firstName", "lastName"})
    private static class Child extends Super {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown=true, value={"id", "createdBy", "updatedBy", "lastUpdated", "created"})
    private static class MfgEtlServiceKeyExtended extends MfgEtlServiceKey {
    }

}
