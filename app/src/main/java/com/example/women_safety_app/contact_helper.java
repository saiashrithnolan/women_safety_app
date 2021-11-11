package com.example.women_safety_app;

public class contact_helper
{
    String name;
    String number;int id;

   public contact_helper()
   {

   }


   public contact_helper(String number,int id)
   {
       this.number = number;
       this.id =id;
   }

   public contact_helper(String number)

   {
       this.number = number;
   }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
