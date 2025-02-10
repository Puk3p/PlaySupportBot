package Puk3p.model;

import java.util.Objects;

public class TicketData {
    private final String user;
    private final String reason;
    private final String staff;
    private final String timestamp;
    private final String category;
    private final String userId;

    public TicketData(String user, String reason, String staff, String timestamp, String category, String userId) {
        this.user = user;
        this.reason = reason;
        this.staff = staff;
        this.timestamp = timestamp;
        this.category = category;
        this.userId = userId;
    }

    public String getUser() { return user; }
    public String getReason() { return reason; }
    public String getStaff() { return staff; }
    public String getTimestamp() { return timestamp; }
    public String getCategory() { return category; }
    public String getUserId() { return userId; }

    @Override
    public String toString() {
        return String.format(
                "User: %s, Reason: %s, Staff: %s, Timestamp: %s, Category: %s",
                user, reason, staff, timestamp, category
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketData that = (TicketData) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(staff, that.staff) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() { return Objects.hash(user, reason, staff, timestamp, category); }
}
