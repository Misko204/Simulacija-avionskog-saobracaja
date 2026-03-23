package model;

public class Aerodrom {
	private String naziv;		// Naziv aerodroma
	private String kod;			// Troslovni kod aerodroma, 3 velika slova
	private double x, y;		// Koordinate aerodroma
	private boolean prikazan;	// Da li je aerodrom prikazan na mapi
	
	public Aerodrom(String naziv, String kod, double x, double y) throws IllegalArgumentException {
		// Ako je naziv prazan, "baca" se greška
		if (naziv == null || naziv.trim().isEmpty()) {
			throw new IllegalArgumentException("Naziv aerodroma ne može biti prazan");
		}
		
		// Ako je kod prazan ili nije 3 velika slova, "baca" se greška
		if (kod == null || kod.length() != 3 || !kod.matches("[A-Z]{3}")) {
			throw new IllegalArgumentException("Kod aerodroma mora biti tačno 3 velika slova");
		}
		
		// Ako su koordinate van opsega, "baca" se greška
		if (x < -90 || x > 90 || y < -90 || y > 90) {
			throw new IllegalArgumentException("Koordinate moraju biti u opsegu [-90, 90]. "
					+ "Uneto: x = " + x + ", y = " + y);
		}
		
		this.naziv = naziv;
		this.kod = kod;
		this.x = x;
		this.y = y;
		this.prikazan = true;
	}
	
	public String getNaziv() {
		return naziv;
	}
	
	public void setNaziv(String naziv) throws IllegalArgumentException {
		if (naziv == null || naziv.trim().isEmpty()) {
			throw new IllegalArgumentException("Naziv aerodroma ne može biti prazan");
		}
		this.naziv = naziv;
	}
	
	public String getKod() {
		return kod;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) throws IllegalArgumentException {
		if (x < -90 || x > 90) {
			throw new IllegalArgumentException("X koordinata mora biti u opsegu [-90, 90]. "
					+ "Uneto: x = " + x);
		}
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) throws IllegalArgumentException {
		if (y < -90 || y > 90) {
			throw new IllegalArgumentException("Y koordinata mora biti u opsegu [-90, 90]. "
					+ "Uneto: y = " + y);
		}
		this.y = y;
	}
	
	@Override
	public String toString() {
		return kod + " - " + naziv + " (" + x + ", " + y + ")";
	}
	
	// Aerodromi su jednaki ako imaju isti kod ili iste koordinate
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || !(obj instanceof Aerodrom)) return false;
		Aerodrom aerodrom = (Aerodrom) obj;
		return kod.equals(aerodrom.kod) || (this.x == aerodrom.x && this.y == aerodrom.y);
	}
	
	public boolean isPrikazan() {
		return prikazan;
	}
	
	public void setPrikazan(boolean prikazan) {
		this.prikazan = prikazan;
	}
	
}
