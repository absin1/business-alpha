package in.talentify.business_alpha;

import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class MediaUploadServices {

	public MediaUploadServices() {
		super();
	}

	public Set<PosixFilePermission> getPermissions() {
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		// add owners permission
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		// add group permissions
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		// add others permissions
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);
		return perms;
	}

}
