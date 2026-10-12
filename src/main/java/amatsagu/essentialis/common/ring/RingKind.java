package amatsagu.essentialis.common.ring;

public enum RingKind {

	CITRINE("citrine_ring", false),
	JADE("jade_ring", false),
	RUBY("ruby_ring", false),
	SAPPHIRE("sapphire_ring", false),
	TANZANITE("tanzanite_ring", false),
	TOPAZ("topaz_ring", false),
	NETHERITE_CITRINE("netherite_citrine_ring", true),
	NETHERITE_JADE("netherite_jade_ring", true),
	NETHERITE_RUBY("netherite_ruby_ring", true),
	NETHERITE_SAPPHIRE("netherite_sapphire_ring", true),
	NETHERITE_TANZANITE("netherite_tanzanite_ring", true),
	NETHERITE_TOPAZ("netherite_topaz_ring", true);

	public final String id;

	public final boolean netherite;

	RingKind(final String id, final boolean netherite) {
		this.id = id;
		this.netherite = netherite;
	}

	public RingFamily family() {
		return RingFamily.valueOf(this.name().replace("NETHERITE_", ""));
	}
}
