package group_3.util;

/*
    @author group3
 */

import group_3.model.Ticket;
import javafx.scene.image.Image;

public final class QRCode {
    private QRCode(){}

    public static String generateTicketQRPayload (Ticket ticket){
        return String.format(
                "{ \"ticketId\": %d, \"attendeeId\": %d, \"eventId\": %d, \"sessionId\": %s }",
                ticket.getTicketID(),
                ticket.getAttendeeID(),
                ticket.getEventID(),
                ticket.getSessionID() == 0 ? "null" : ticket.getSessionID()
        );
    }

    public static Image generateQRImage(String text, int width, int height) {
        try {
            // We construct a URL that points to a free QR code generator
            String apiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=" + width + "x" + height + "&data=" + text;

            // JavaFX loads the image directly from that URL in the background
            // 'true' = load in background (async) so the app doesn't freeze
            return new Image(apiUrl, true);

        } catch (Exception e) {
            System.out.println("Error fetching QR Code: " + e.getMessage());
            return null;
        }
    }
}
