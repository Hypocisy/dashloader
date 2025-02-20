package dev.notalpha.dashloader.client.identifier;


import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.mixin.accessor.ModelIdentifierAccessor;
import net.minecraft.client.resources.model.ModelResourceLocation;

public final class DashModelResourceLocation implements DashObject<ModelResourceLocation, ModelResourceLocation> {
	public final String namespace;
	public final String path;
	public final String variant;

	public DashModelResourceLocation(ModelResourceLocation identifier) {
		this.namespace = identifier.getNamespace();
		this.path = identifier.getPath();
		this.variant = identifier.getVariant();
	}

	public DashModelResourceLocation(String namespace, String path, String variant) {
		this.namespace = namespace;
		this.path = path;
		this.variant = variant;
	}

	@Override
	public ModelResourceLocation export(RegistryReader exportHandler) {
		return ModelIdentifierAccessor.init(namespace, path, variant, null);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashModelResourceLocation that = (DashModelResourceLocation) o;

		if (!namespace.equals(that.namespace)) return false;
		if (!path.equals(that.path)) return false;
		return variant.equals(that.variant);
	}

	@Override
	public int hashCode() {
		int result = namespace.hashCode();
		result = 31 * result + path.hashCode();
		result = 31 * result + variant.hashCode();
		return result;
	}
}
