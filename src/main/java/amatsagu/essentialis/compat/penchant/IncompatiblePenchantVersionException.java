package amatsagu.essentialis.compat.penchant;

public class IncompatiblePenchantVersionException extends RuntimeException {

	public IncompatiblePenchantVersionException(final String foundVersion) {
		super(
			"Essentialis requires Penchant " + PenchantCompat.MIN_VERSION + " or newer for chakram enchantment leveling, but found " + foundVersion + ". Please update Penchant."
		);
	}
}
