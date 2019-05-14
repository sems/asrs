package resources;

import Logic.Order;
import Logic.OrderItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

public class pdf {
    private Order order;
    private ArrayList<OrderItem> items;
    private Document document;
    private Font font;
    private String filename;
    private Date today;

    public pdf(Order order) {
        this.order = order;
        this.items = order.getOrderItems();
        this.document = new Document();
        this.font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        this.filename = "order-" + order.getId() + ".pdf";
        this.today = Calendar.getInstance().getTime();
    }

    public void generate() throws FileNotFoundException, DocumentException {
        PdfWriter.getInstance(this.document, new FileOutputStream(this.filename));
        this.document.open();

        this.addText("Order: " + this.order.getId());

        String pattern = "MM/dd/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);

        this.addText("Datum: " + df.format(today));
        document.add( Chunk.NEWLINE );
        this.addText(this.order.getBuyer());
        this.addText(this.order.getAddr());
        document.add( Chunk.NEWLINE );

        this.addText("Producten:");
        document.add( Chunk.NEWLINE );

        PdfPTable table = new PdfPTable(3);
        table.setWidths(new float[] { 15, 70, 15 });
        addTableHeader(table);
        for (OrderItem item: this.items ) {
            addRows(table, item);
        }
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);
        document.add(table);

        this.document.close();
    }

    private void addText(String text){
        Paragraph chunk = new Paragraph(text, this.font);
        try {
            this.document.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Product", "Aantal")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle, this.font));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, OrderItem item) {
        table.addCell(new Phrase(String.valueOf(item.getStorageItemID()), this.font));
        table.addCell(new Phrase(item.getName(), this.font));
        table.addCell(new Phrase(String.valueOf(item.getQuantity()), this.font));
    }
}
