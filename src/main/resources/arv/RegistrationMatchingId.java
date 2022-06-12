//package ch.redanz.redanzCore.model.matching;
//
//import javax.persistence.Embeddable;
//import java.io.Serializable;
//import java.util.Objects;
//
//@Embeddable
//public class RegistrationMatchingId implements Serializable {
//
//    private Long registration1Id;
//    private Long registration2Id;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof RegistrationMatchingId)) return false;
//        RegistrationMatchingId that = (RegistrationMatchingId) o;
//        return registration1Id.equals(that.registration1Id) && registration2Id.equals(that.registration2Id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(registration1Id, registration2Id);
//    }
//}
